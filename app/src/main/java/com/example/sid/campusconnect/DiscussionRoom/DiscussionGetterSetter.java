package com.example.sid.campusconnect.DiscussionRoom;

/**
 * Created by Sid on 06-Dec-15.
 */

// old java style programming :) first love :)

public class DiscussionGetterSetter {

    private String subject;
    private String status;
    private String disid;

    public DiscussionGetterSetter(String subject, String status,String disid) {
        super();
        this.subject = subject;
        this.status = status;
        this.disid=disid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status)
    {
        this.status= status;
    }

    public String getDisid()
    {
        return disid;
    }

    public void setDisid(String disid)
    {
        this.disid=disid;
    }

}
