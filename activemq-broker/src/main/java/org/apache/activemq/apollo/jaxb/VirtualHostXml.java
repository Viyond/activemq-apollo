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
package org.apache.activemq.apollo.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.activemq.apollo.broker.VirtualHost;
import org.apache.activemq.protobuf.AsciiBuffer;

@XmlRootElement(name = "virtual-host")
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualHostXml {

	public static class AsciiBufferAdapter extends XmlAdapter<String, AsciiBuffer> {
		@Override
		public String marshal(AsciiBuffer v) throws Exception {
			return v.toString();
		}
		@Override
		public AsciiBuffer unmarshal(String v) throws Exception {
			return new AsciiBuffer(v);
		}
	}
	
    @XmlJavaTypeAdapter(AsciiBufferAdapter.class)
    @XmlElement(name="host-name", required=true)
    private ArrayList<AsciiBuffer> hostNames = new ArrayList<AsciiBuffer>();

	public VirtualHost createVirtualHost(BrokerXml brokerXml) {
		VirtualHost rc = new VirtualHost();
		rc.setHostNames(hostNames);
		return rc;
	}

}