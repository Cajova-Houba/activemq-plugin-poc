#!/usr/bin/env bash

activemqDir="/home/zdenda/tools/activemq/apache-activemq-5.15.11"

# copy jar with plugin
cp target/ActiveMQPluginTest-1.0-SNAPSHOT.jar "${activemqDir}/lib"

# copy configuration
cp -r deploy/* "${activemqDir}"