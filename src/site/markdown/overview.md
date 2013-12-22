# Overview

The ElasticServices project is a collection of libraries, tools and components that can be used together to create asynchronous services that communicate with each other.

A **component** is essentially a daemon that has one or more services running on it. Components are identified by a three part **component ref** that consists of a site id, cluster id and component id. A **service** is an instance of a service implementation associated with a component and is identified by a two part **service ref** consisting of a component and a service id.

When a service wants to engage another service, it uses the **service registry** to send a **message** to the service identified by the **service ref**.

An example service implementation is the **gossip service**. It consists of a service implementation that listens to **component online** and **component status** messages that are broadcasts to a fanout topic as well as a broadcast **work** that periodically sends out those messages for other components to consume.
