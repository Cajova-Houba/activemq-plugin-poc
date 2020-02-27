# Security plugin for ActiveMQ

Contains authentication and authorization brokers used to integrate ActiveMQ message broker with Membernet.

## How to use

Build plugins using 

    mvn package
    
Then deploy by copying jar (the one with all dependencies to the /lib folder of your ActiveMQ installation. And contents of folder deploy into root of your ActiveMQ installation.