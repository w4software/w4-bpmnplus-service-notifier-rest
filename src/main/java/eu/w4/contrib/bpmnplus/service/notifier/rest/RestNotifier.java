package eu.w4.contrib.bpmnplus.service.notifier.rest;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import eu.w4.common.configuration.Configuration;
import eu.w4.common.exception.CheckedException;
import eu.w4.common.log.Logger;
import eu.w4.common.log.LoggerFactory;
import eu.w4.engine.client.bpmn.foundation.ExtensionElement;
import eu.w4.engine.client.bpmn.infrastructure.Definitions;
import eu.w4.engine.client.bpmn.w4.infrastructure.DefinitionsExtensionElement;
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstance;
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstanceAttachment;
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstanceIdentifier;
import eu.w4.engine.client.bpmn.w4.runtime.EventInstance;
import eu.w4.engine.client.bpmn.w4.runtime.EventInstanceAttachment;
import eu.w4.engine.client.bpmn.w4.runtime.EventInstanceIdentifier;
import eu.w4.engine.client.bpmn.w4.runtime.ProcessInstance;
import eu.w4.engine.client.bpmn.w4.runtime.ProcessInstanceAttachment;
import eu.w4.engine.core.bpmn.service.AbstractService;
import eu.w4.engine.core.bpmn.service.ConfigurationFileNames;
import eu.w4.engine.core.bpmn.service.ExecutionContext;
import eu.w4.engine.core.bpmn.service.Name;
import eu.w4.engine.core.bpmn.service.Result;
import eu.w4.engine.core.bpmn.service.Scope;
import eu.w4.engine.core.bpmn.service.Version;

@Name("RestNotifier")
@Version("1.0")
@ConfigurationFileNames({ "targets" })
public class RestNotifier extends AbstractService
{

  private Logger _logger = LoggerFactory.getLogger(RestNotifier.class.getName());
  private ExecutionContext _executionContext;
  private Configuration _configuration;
  private Client _restClient;

  private ProcessInstanceAttachment _processInstanceAttachment;
  private ActivityInstanceAttachment _activityInstanceAttachment;
  private EventInstanceAttachment _eventInstanceAttachment;

  @Override
  public void afterInit(Scope scope, ExecutionContext executionContext)
      throws CheckedException, RemoteException
  {
    super.afterInit(scope, executionContext);
    _executionContext = executionContext;
    _configuration = getConfiguration();
    _restClient = ClientBuilder.newClient();

    _processInstanceAttachment = createEmptyProcessInstanceAttachment();
    _processInstanceAttachment.setProcessDescriptorAttached(true);
    _activityInstanceAttachment = createEmptyActivityInstanceAttachment();
    _activityInstanceAttachment.setProcessInstanceAttached(true);
    _activityInstanceAttachment.setProcessInstanceAttachment(_processInstanceAttachment);
    _activityInstanceAttachment.setActivityDescriptorAttached(true);
    _eventInstanceAttachment = createEmptyEventInstanceAttachment();
    _eventInstanceAttachment.setProcessInstanceAttached(true);
    _eventInstanceAttachment.setProcessInstanceAttachment(_processInstanceAttachment);
    _eventInstanceAttachment.setEventDescriptorAttached(true);
  }

  @Override
  public ProcessInstanceAttachment getProcessInstanceAttachment()
      throws CheckedException, RemoteException
  {
    return _processInstanceAttachment;
  }

  @Override
  public ActivityInstanceAttachment getActivityInstanceAttachment()
      throws CheckedException, RemoteException
  {
    return _activityInstanceAttachment;
  }

  @Override
  public EventInstanceAttachment getEventInstanceAttachment()
      throws CheckedException, RemoteException
  {
    return _eventInstanceAttachment;
  }

  @Override
  public Result execute()
    throws CheckedException, RemoteException
  {
    final Notification notification = new Notification();
    final Definitions definitions = _executionContext.getDefinitions();
    final ActivityInstanceIdentifier activityInstanceIdentifier = _executionContext.getActivityInstanceIdentifier();
    final EventInstanceIdentifier eventInstanceIdentifier = _executionContext.getEventInstanceIdentifier();

    String trigger = _executionContext.getNotificationTrigger().toString().substring(3);
    if (definitions != null)
    {
      String definitionsVersion = null;
      for (ExtensionElement ee : definitions.getExtensionElements())
      {
        if (ee instanceof DefinitionsExtensionElement)
        {
          DefinitionsExtensionElement dee = (DefinitionsExtensionElement) ee;
          definitionsVersion = dee.getVersion();
        }
      }
      notification.setDefinitionsId(definitions.getId());
      notification.setDefinitionsVersion(definitionsVersion);
    }
    if (eventInstanceIdentifier != null)
    {
      EventInstance eventInstance = getEventInstance();
      notification.setEventInstanceId(Long.toString(eventInstance.getIdentifier().getId()));
      notification.setEventId(eventInstance.getEventDescriptor().getId());
      notification.setProcessId(eventInstance.getProcessInstance().getProcessDescriptor().getId());
      notification.setProcessInstanceId(Long.toString(eventInstance.getProcessInstance().getIdentifier().getId()));
      trigger = "EVENT_INSTANCE_" + trigger;
    }
    else if (activityInstanceIdentifier != null)
    {
      ActivityInstance activityInstance = getActivityInstance();
      notification.setActivityInstanceId(Long.toString(activityInstance.getIdentifier().getId()));
      notification.setActivityId(activityInstance.getActivityDescriptor().getId());
      notification.setProcessId(activityInstance.getProcessInstance().getProcessDescriptor().getId());
      notification.setProcessInstanceId(Long.toString(activityInstance.getProcessInstance().getIdentifier().getId()));
      trigger = "ACTIVITY_INSTANCE_" + trigger;
    }
    else
    {
      ProcessInstance processInstance = getProcessInstance();
      notification.setProcessInstanceId(Long.toString(processInstance.getIdentifier().getId()));
      notification.setProcessId(processInstance.getProcessDescriptor().getId());
      trigger = "PROCESS_INSTANCE_" + trigger;
    }

    final NotificationType notificationType;
    try
    {
      notificationType = NotificationType.valueOf(trigger);
    }
    catch (IllegalArgumentException e)
    {
      _logger.error("Unknown trigger " + trigger);
      return null;
    }
    notification.setType(notificationType);

    final List<String> targets = new ArrayList<String>();

    final List<String> propertyKeys = new ArrayList<String>();
    propertyKeys.add("target");
    propertyKeys.add("target." + notification.getDefinitionsId());
    propertyKeys.add("target." + notification.getDefinitionsId() + "." + notification.getDefinitionsVersion());
    for (final String propertyKey : propertyKeys)
    {
      if (_configuration.getKeys().contains(propertyKey))
      {
        final List<String> configuredTargets = _configuration.getValues(propertyKey);
        if (configuredTargets != null)
        {
          targets.addAll(configuredTargets);
        }
      }
    }

    for (final String target : targets)
    {
      final WebTarget webTarget = _restClient.target(target);
      webTarget.request().post(Entity.entity(notification, MediaType.APPLICATION_JSON));
    }

    return null;
  }
}
