package io.bootique.tools.release.model.maven.persistent.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.Property;

import io.bootique.tools.release.model.maven.persistent.Dependency;
import io.bootique.tools.release.model.maven.persistent.Project;

/**
 * Class _Module was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Module extends BaseDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> GROUP_STR = Property.create("groupStr", String.class);
    public static final Property<String> GITHUB_ID = Property.create("githubId", String.class);
    public static final Property<String> VERSION = Property.create("version", String.class);
    public static final Property<List<Dependency>> DEPENDENCIES = Property.create("dependencies", List.class);
    public static final Property<List<Dependency>> MODULE = Property.create("module", List.class);
    public static final Property<Project> MODULES = Property.create("modules", Project.class);
    public static final Property<Project> PROJECT = Property.create("project", Project.class);

    protected String groupStr;
    protected String githubId;
    protected String version;

    protected Object dependencies;
    protected Object module;
    protected Object modules;
    @JsonIgnore
    protected Object project;

    public void setGroupStr(String groupStr) {
        beforePropertyWrite("groupStr", this.groupStr, groupStr);
        this.groupStr = groupStr;
    }

    public String getGroupStr() {
        beforePropertyRead("groupStr");
        return this.groupStr;
    }

    public void setGithubId(String githubId) {
        beforePropertyWrite("githubId", this.githubId, githubId);
        this.githubId = githubId;
    }

    public String getGithubId() {
        beforePropertyRead("githubId");
        return this.githubId;
    }

    public void setVersion(String version) {
        beforePropertyWrite("version", this.version, version);
        this.version = version;
    }

    public String getVersion() {
        beforePropertyRead("version");
        return this.version;
    }

    public void addToDependencies(Dependency obj) {
        addToManyTarget("dependencies", obj, true);
    }

    public void removeFromDependencies(Dependency obj) {
        removeToManyTarget("dependencies", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<Dependency> getDependencies() {
        return (List<Dependency>)readProperty("dependencies");
    }

    public void addToModule(Dependency obj) {
        addToManyTarget("module", obj, true);
    }

    public void removeFromModule(Dependency obj) {
        removeToManyTarget("module", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<Dependency> getModule() {
        return (List<Dependency>)readProperty("module");
    }

    public void setModules(Project modules) {
        setToOneTarget("modules", modules, true);
    }

    public Project getModules() {
        return (Project)readProperty("modules");
    }

    public void setProject(Project project) {
        setToOneTarget("project", project, true);
    }

    public Project getProject() {
        return (Project)readProperty("project");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "groupStr":
                return this.groupStr;
            case "githubId":
                return this.githubId;
            case "version":
                return this.version;
            case "dependencies":
                return this.dependencies;
            case "module":
                return this.module;
            case "modules":
                return this.modules;
            case "project":
                return this.project;
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
            case "groupStr":
                this.groupStr = (String)val;
                break;
            case "githubId":
                this.githubId = (String)val;
                break;
            case "version":
                this.version = (String)val;
                break;
            case "dependencies":
                this.dependencies = val;
                break;
            case "module":
                this.module = val;
                break;
            case "modules":
                this.modules = val;
                break;
            case "project":
                this.project = val;
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
        out.writeObject(this.groupStr);
        out.writeObject(this.githubId);
        out.writeObject(this.version);
        out.writeObject(this.dependencies);
        out.writeObject(this.module);
        out.writeObject(this.modules);
        out.writeObject(this.project);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.groupStr = (String)in.readObject();
        this.githubId = (String)in.readObject();
        this.version = (String)in.readObject();
        this.dependencies = in.readObject();
        this.module = in.readObject();
        this.modules = in.readObject();
        this.project = in.readObject();
    }

}