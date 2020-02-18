#!/usr/bin/env bash

activemqDir=""

cp target/ActiveMQPluginTest-1.0-SNAPSHOT.jar "${activemqDir}/lib"

copy conf/activemq.xml "${activemqDir}/conf"