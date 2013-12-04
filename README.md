# About

This library provides a protocol buffer based framework for creating self
coordinating asynchronous services.

# Overview

The library provides tools and components to create and define services that
can communicate with each other.

A **component** is essentially a daemon that has one or more services running
on it. Components are identified by a three part **component ref** that
consists of a site id, cluster id and component id. A **service** is an
instance of a service implementation associated with a component and is
identified by a two part **service ref** consisting of a component and a
service id.

When a service wants to engage another service, it uses the **service
registry** to send a **message** to the service identified by the **service
ref**.

An example service implementation is the **gossip service**. It consists of a
service implementation that listens to **component online** and **component
status** messages that are broadcasts to a fanout topic as well as a
broadcast **work** that periodically sends out those messages for other
components to consume.

## Example: Calc

In the examples directory are the calc-service and calc-webapp maven modules.

The calc-service module includes a calculation service that listens to add
messages and responds with result messages. It is an example of a standalone
JSVC based daemon.

The calc-webapp module includes a spring-based HTTP API that engages the calc
service. It includes an example edge service that demonstrates how
synchronous projects, like HTTP edges, can engage asynchronous services.

# License

Copyright (c) 2013 Nick Gerakines <nick@gerakines.net> and Chris
Antenesse <chris@antenesse.net>

This project and its contents are open source under the MIT license.

