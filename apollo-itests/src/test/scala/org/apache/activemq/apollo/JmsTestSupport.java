/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.apollo;

import javax.jms.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test cases used to test the JMS message consumer.
 * 
 * 
 */
public class JmsTestSupport extends TestSupport {

    static final private AtomicLong TEST_COUNTER = new AtomicLong();
    public String userName;
    public String password;
    public String messageTextPrefix = "";

    protected ConnectionFactory factory;
    protected Connection connection;

    protected List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());

    enum DestinationType {
        QUEUE_TYPE,
        TOPIC_TYPE,
        TEMP_QUEUE_TYPE,
        TEMP_TOPIC_TYPE
    }

    // /////////////////////////////////////////////////////////////////
    //
    // Test support methods.
    //
    // /////////////////////////////////////////////////////////////////
    protected Destination createDestination(Session session, DestinationType type) throws JMSException {
        String testMethod = getName();
        if( testMethod.indexOf(" ")>0 ) {
            testMethod = testMethod.substring(0, testMethod.indexOf(" "));
        }
        String name = "TEST." + getClass().getName() + "." +testMethod+"."+TEST_COUNTER.getAndIncrement();
        switch (type) {
        case QUEUE_TYPE:
            return session.createQueue(name);
        case TOPIC_TYPE:
            return session.createTopic(name);
        case TEMP_QUEUE_TYPE:
            return session.createTemporaryQueue();
        case TEMP_TOPIC_TYPE:
            return session.createTemporaryTopic();
        default:
            throw new IllegalArgumentException("type: " + type);
        }
    }

    protected void sendMessages(Destination destination, int count) throws Exception {
        ConnectionFactory factory = createConnectionFactory();
        Connection connection = factory.createConnection();
        connection.start();
        sendMessages(connection, destination, count);
        connection.close();
    }

    protected void sendMessages(Connection connection, Destination destination, int count) throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        sendMessages(session, destination, count);
        session.close();
    }

    protected void sendMessages(Session session, Destination destination, int count) throws JMSException {
        MessageProducer producer = session.createProducer(destination);
        for (int i = 0; i < count; i++) {
            producer.send(session.createTextMessage(messageTextPrefix  + i));
        }
        producer.close();
    }

    public ConnectionFactory createConnectionFactory() throws Exception {
        return broker.getConnectionFactory();
    }

    protected void setUp() throws Exception {
        super.setUp();

        if (System.getProperty("basedir") == null) {
            File file = new File(".");
            System.setProperty("basedir", file.getAbsolutePath());
        }

        broker.start();
        factory = createConnectionFactory();
        connection = factory.createConnection(userName, password);
        connections.add(connection);
    }

    protected void tearDown() throws Exception {
        for (Iterator iter = connections.iterator(); iter.hasNext();) {
            Connection conn = (Connection)iter.next();
            try {
                conn.close();
            } catch (Throwable e) {
            }
            iter.remove();
        }
        broker.stop();
        super.tearDown();
    }

    protected void safeClose(Connection c) {
        try {
            c.close();
        } catch (Throwable e) {
        }
    }

    protected void safeClose(Session s) {
        try {
            s.close();
        } catch (Throwable e) {
        }
    }

    protected void safeClose(MessageConsumer c) {
        try {
            c.close();
        } catch (Throwable e) {
        }
    }

    protected void safeClose(MessageProducer p) {
        try {
            p.close();
        } catch (Throwable e) {
        }
    }

    protected void profilerPause(String prompt) throws IOException {
        if (System.getProperty("profiler") != null) {
            pause(prompt);
        }
    }

    protected void pause(String prompt) throws IOException {
        System.out.println();
        System.out.println(prompt + "> Press enter to continue: ");
        while (System.in.read() != '\n') {
        }
    }

}