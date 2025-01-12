package io.bootique.tools.release.service.job;

import io.bootique.tools.release.model.job.BatchJob;
import io.bootique.tools.release.model.job.BatchJobDescriptor;
import io.bootique.tools.release.model.job.BatchJobResult;
import io.bootique.tools.release.service.preferences.PreferenceService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class DefaultBatchJobService implements BatchJobService {

    private final static AtomicLong JOB_ID_SEQUENCE = new AtomicLong();

    private final PreferenceService preferences;
    private final Map<Long, BatchJob<?, ?>> jobMap = new ConcurrentHashMap<>();
    private final BatchJobExecutor executor = new BatchJobExecutor();

    private volatile long lastCleanupAtId;

    @Inject
    public DefaultBatchJobService(PreferenceService preferences) {
        this.preferences = preferences;
    }

    @Override
    public <T, R> BatchJob<T, R> submit(BatchJobDescriptor<T, R> descriptor) {
        cleanUp();

        List<BatchJobTask<BatchJobResult<T, R>>> tasks = descriptor.getData().stream()
                .map(data -> executor.submit(new BatchJobResultCallable<>(executor, descriptor, data)))
                .map(future -> (BatchJobTask<BatchJobResult<T, R>>)future)
                .collect(Collectors.toList());

        BatchJob<T, R> job = new BatchJob<>(JOB_ID_SEQUENCE.getAndIncrement(), tasks, descriptor);
        jobMap.put(job.getId(), job);
        return job;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> BatchJob<T, R> getJobById(long id) {
        return (BatchJob<T, R>)jobMap.get(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> BatchJob<T, R> getCurrentJob() {
        return preferences.have(BatchJobService.CURRENT_JOB_ID)
                ? getJobById(preferences.get(BatchJobService.CURRENT_JOB_ID))
                : null;
    }

    private void cleanUp() {
        if(lastCleanupAtId != JOB_ID_SEQUENCE.get()) {
            lastCleanupAtId = JOB_ID_SEQUENCE.get();
            jobMap.entrySet().removeIf(entry -> entry.getValue().isDone());
        }
        executor.checkState();
    }

}
