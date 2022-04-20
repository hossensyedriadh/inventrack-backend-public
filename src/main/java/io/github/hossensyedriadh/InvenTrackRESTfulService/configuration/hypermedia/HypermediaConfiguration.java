package io.github.hossensyedriadh.InvenTrackRESTfulService.configuration.hypermedia;

import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaMappingInformation;
import org.springframework.hateoas.support.WebStack;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableHypermediaSupport(type = {EnableHypermediaSupport.HypermediaType.HAL}, stacks = WebStack.WEBMVC)
public class HypermediaConfiguration implements HypermediaMappingInformation {
    /**
     * All {@link MediaType}s this hypermedia can handle.
     *
     * @return java.util.List<MediaType>
     */
    @Override
    public List<MediaType> getMediaTypes() {
        List<MediaType> mediaTypes = new ArrayList<>();

        mediaTypes.add(MediaTypes.HAL_JSON);
        return mediaTypes;
    }
}
