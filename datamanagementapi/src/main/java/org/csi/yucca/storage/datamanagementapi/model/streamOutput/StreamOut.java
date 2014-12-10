
package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class StreamOut {

	private String id;
	
    @Expose
    private Integer idStream;
    @Expose
    private String streamCode;
    @Expose
    private String streamName;
    @Expose
    private ConfigData configData;
    @Expose
    private Streams streams;
    
    
    public static StreamOut fromJson(String json) {
		Gson gson = JSonHelper.getInstance();
		return gson.fromJson(json, StreamOut.class);
	}

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    /**
     * 
     * @return
     *     The idStream
     */
    public Integer getIdStream() {
        return idStream;
    }

    /**
     * 
     * @param idStream
     *     The idStream
     */
    public void setIdStream(Integer idStream) {
        this.idStream = idStream;
    }

    /**
     * 
     * @return
     *     The streamCode
     */
    public String getStreamCode() {
        return streamCode;
    }

    /**
     * 
     * @param streamCode
     *     The streamCode
     */
    public void setStreamCode(String streamCode) {
        this.streamCode = streamCode;
    }

    /**
     * 
     * @return
     *     The streamName
     */
    public String getStreamName() {
        return streamName;
    }

    /**
     * 
     * @param streamName
     *     The streamName
     */
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    /**
     * 
     * @return
     *     The configData
     */
    public ConfigData getConfigData() {
        return configData;
    }

    /**
     * 
     * @param configData
     *     The configData
     */
    public void setConfigData(ConfigData configData) {
        this.configData = configData;
    }

    /**
     * 
     * @return
     *     The streams
     */
    public Streams getStreams() {
        return streams;
    }

    /**
     * 
     * @param streams
     *     The streams
     */
    public void setStreams(Streams streams) {
        this.streams = streams;
    }

	

}
