package org.valesz.activemq.service.tronalddump;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TronaldDumpQuote {

    private String quoteId;

    private String value;



    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TronaldDumpQuote{" +
                "quoteId='" + quoteId + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
