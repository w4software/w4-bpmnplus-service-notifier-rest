package eu.w4.contrib.bpmnplus.service.notifier.rest;

/**
 * Payload posted in JSON format to REST endpoints
 */
public class Notification
{

  private NotificationType _type;
  private String _definitionsId;
  private String _definitionsVersion;
  private String _processId;
  private String _processInstanceId;
  private String _activityId;
  private String _activityInstanceId;
  private String _eventId;
  private String _eventInstanceId;

  public NotificationType getType()
  {
    return _type;
  }

  public void setType(NotificationType type)
  {
    _type = type;
  }

  public String getDefinitionsId()
  {
    return _definitionsId;
  }

  public void setDefinitionsId(String definitionsId)
  {
    _definitionsId = definitionsId;
  }

  public String getDefinitionsVersion()
  {
    return _definitionsVersion;
  }

  public void setDefinitionsVersion(String definitionsVersion)
  {
    _definitionsVersion = definitionsVersion;
  }

  public String getProcessId()
  {
    return _processId;
  }

  public void setProcessId(String processId)
  {
    _processId = processId;
  }

  public String getProcessInstanceId()
  {
    return _processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    _processInstanceId = processInstanceId;
  }

  public String getActivityId()
  {
    return _activityId;
  }

  public void setActivityId(String activityId)
  {
    _activityId = activityId;
  }

  public String getActivityInstanceId()
  {
    return _activityInstanceId;
  }

  public void setActivityInstanceId(String activityInstanceId)
  {
    _activityInstanceId = activityInstanceId;
  }

  public String getEventId()
  {
    return _eventId;
  }

  public void setEventId(String eventId)
  {
    _eventId = eventId;
  }

  public String getEventInstanceId()
  {
    return _eventInstanceId;
  }

  public void setEventInstanceId(String eventInstanceId)
  {
    _eventInstanceId = eventInstanceId;
  }

}
