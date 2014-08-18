package org.celstec.dao.gen;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import org.celstec.arlearn2.beans.run.Message;
// KEEP INCLUDES END
/**
 * Entity mapped to table MESSAGE_LOCAL_OBJECT.
 */
public class MessageLocalObject {

    private Long id;
    private String subject;
    private String body;
    private String author;
    private Boolean synced;
    private Long time;
    private String userIds;
    private long threadId;
    private long runId;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public MessageLocalObject() {
    }

    public MessageLocalObject(Long id) {
        this.id = id;
    }

    public MessageLocalObject(Long id, String subject, String body, String author, Boolean synced, Long time, String userIds, long threadId, long runId) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.author = author;
        this.synced = synced;
        this.time = time;
        this.userIds = userIds;
        this.threadId = threadId;
        this.runId = runId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    // KEEP METHODS - put your custom methods here
    public Message getBean(boolean copyId){
        Message message = new Message();
        if (copyId) message.setMessageId(getId());
        message.setBody(getBody());
        message.setDate(getTime());
        message.setThreadId(getThreadId());
        message.setRunId(getRunId());
        message.setSubject(getSubject());
        return message;
    }
    // KEEP METHODS END

}
