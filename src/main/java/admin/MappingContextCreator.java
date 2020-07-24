package admin;


import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.util.Lazy;

public class MappingContextCreator {

    protected final Lazy<ElasticsearchConverter> elasticsearchConverter = Lazy.of(this::setupElasticsearchConverter);

    private ElasticsearchConverter setupElasticsearchConverter() {
        final MappingElasticsearchConverter converter = new MappingElasticsearchConverter(this.setupMappingContext());
        // todo this is logged in CustomConversions, because
        //  if (LOG.isWarnEnabled() && !converterRegistration.isSimpleSourceType())
        //  since we don't use geo stuff this shouldn't be important?
        //  Registering GeoConverters.getConvertersToRegister() as below doesn't work.
        //  Looks like they're added by default anyway. Not sure. Should check again.
//        final ElasticsearchCustomConversions customConversions = new ElasticsearchCustomConversions(GeoConverters.getConvertersToRegister());
//        converter.setConversions(customConversions);
        return converter;
    }

    private SimpleElasticsearchMappingContext setupMappingContext() {

        final SimpleElasticsearchMappingContext mappingContext = new ElasticsearchConfigurationSupport()
                .elasticsearchMappingContext();
        mappingContext.initialize();
        return mappingContext;
    }

    final MappingBuilder getMappingBuilder() {
        return new MappingBuilder(this.elasticsearchConverter.get());
    }

}
