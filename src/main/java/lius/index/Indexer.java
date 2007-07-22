package lius.index;

import java.util.Collection;
import org.springframework.core.io.Resource;

import lius.config.LiusConfig;

public interface Indexer {
    public int getType();

    public boolean isConfigured(LiusConfig liusConfig);

    public Collection getConfigurationFields(LiusConfig liusConfig);

    public ParsingResult parseResource(LiusConfig liusConfig,
            Resource resource);

    public ParsingResult parseObject(
            LiusConfig liusConfig, Object resource);

    public String getMimeType(Resource resource);
}