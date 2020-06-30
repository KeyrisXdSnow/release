package io.bootique.tools.release.model.persistent.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.cayenne.exp.Property;

import io.bootique.tools.release.model.persistent.Label;
import io.bootique.tools.release.model.persistent.Repository;
import io.bootique.tools.release.model.persistent.RepositoryNode;
import io.bootique.tools.release.model.persistent.User;

/**
 * Class _PullRequest was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PullRequest extends RepositoryNode {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_PK_COLUMN = "ID_PK";

    public static final Property<User> AUTHOR = Property.create("author", User.class);
    public static final Property<List<Label>> LABELS = Property.create("labels", List.class);
    public static final Property<Repository> REPOSITORY = Property.create("repository", Repository.class);


    protected Object author;
    protected Object labels;
    @JsonIgnore
    protected Object repository;

    public void setAuthor(User author) {
        setToOneTarget("author", author, true);
    }

    public User getAuthor() {
        return (User)readProperty("author");
    }

    public void addToLabels(Label obj) {
        addToManyTarget("labels", obj, true);
    }

    public void removeFromLabels(Label obj) {
        removeToManyTarget("labels", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<Label> getLabels() {
        return (List<Label>)readProperty("labels");
    }

    public void setRepository(Repository repository) {
        setToOneTarget("repository", repository, true);
    }

    public Repository getRepository() {
        return (Repository)readProperty("repository");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "author":
                return this.author;
            case "labels":
                return this.labels;
            case "repository":
                return this.repository;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "author":
                this.author = val;
                break;
            case "labels":
                this.labels = val;
                break;
            case "repository":
                this.repository = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.author);
        out.writeObject(this.labels);
        out.writeObject(this.repository);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.author = in.readObject();
        this.labels = in.readObject();
        this.repository = in.readObject();
    }

}
