w4-bpmnplus-service-notifier-rest
=================================

This module is a notification service implementation for W4 BPMN+ that allow to send REST/JSON POST calls to any number of endpoints
each time this notification is used.


Download
--------

The package is available from the following locations:
 
 - [bpmnplus-service-notifier-rest-1.0.0-service.zip](http://maven.w4store.com/repository/contrib/eu/w4/contrib/bpmnplus-service-notifier-rest/1.0.0/bpmnplus-service-notifier-rest-1.0.0-service.zip) 
 - [bpmnplus-service-notifier-rest-1.0.0-service.tar.gz](http://maven.w4store.com/repository/contrib/eu/w4/contrib/bpmnplus-service-notifier-rest/1.0.0/bpmnplus-service-notifier-rest-1.0.0-service.tar.gz) 


Installation
------------

### Extraction

Extract the package, either zip or tar.gz, at the root of a W4 BPMN+ Engine installation. It will create the necessary entries into `services` subdirectory of W4 BPMN+ Engine.

### Configuration

Locate and modify the file `W4BPMPLUS_HOME/services/bpmnplus-service-notifier-rest-1.0.0/conf/targets.properties` 
to configure endpoints that should be called.

You can add one REST endpoint target per line using one of the following three forms

 - `target=http://my.end.point/path` to call the endpoint each time the notifier is used.
 - `target.definitionId=http://my.end.point/path` to call the endpoint for notifications coming for a particular definition only
 - `target.definitionId.definitionVersion=http://my.end.point/path` to call the endpoint for a particular definition and version only


Usage
-----

In BPMN+ Composer, this notifier can be used on any definition by adding a Notification extension (in extension property sheet) on processes, activities or events,
and by specifying a service named "RestNotifier" with version "1.0".


License
-------

Copyright (c) 2015, W4 S.A. 

This project is licensed under the terms of the MIT License (see LICENSE file)

Ce projet est licencié sous les termes de la licence MIT (voir le fichier LICENSE)
