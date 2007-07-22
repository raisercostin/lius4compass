package lius.index;

import java.util.Collection;

public interface Indexer {
    public int getType();

    public boolean isConfigured();

    public Collection getConfigurationFields();

    public String getContent();

    public Collection getPopulatedLiusFields();
}