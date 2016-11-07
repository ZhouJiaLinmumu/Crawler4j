/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.frontier;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
public class WebURLTupleBinding extends TupleBinding<WebURL> {

	//这两个函数主要是将WebURL对象中的数据读入或读出到TupleInput中去
	
  @Override
  public WebURL entryToObject(TupleInput input) {
    WebURL webURL = new WebURL();
    webURL.setURL(input.readString());
    webURL.setDocid(input.readInt());
    webURL.setParentDocid(input.readInt());
    webURL.setParentUrl(input.readString());
    webURL.setDepth(input.readShort());
    webURL.setPriority(input.readByte());
    webURL.setAnchor(input.readString());
    return webURL;
  }

  @Override
  public void objectToEntry(WebURL url, TupleOutput output) {
    output.writeString(url.getURL());
    output.writeInt(url.getDocid());
    output.writeInt(url.getParentDocid());
    output.writeString(url.getParentUrl());
    output.writeShort(url.getDepth());
    output.writeByte(url.getPriority());
    output.writeString(url.getAnchor());
  }
}