package org.licket.spring.resource;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static org.licket.core.resource.css.StylesheetResource.CSS_MIMETYPE;
import static org.licket.core.resource.javascript.JavascriptStaticResource.JAVASCRIPT_MIMETYPE;
import static org.licket.core.view.LicketUrls.CONTEXT_RESOURCES;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.servlet.ServletContext;

import org.licket.core.resource.FootParticipatingResource;
import org.licket.core.resource.HeadParticipatingResource;
import org.licket.core.resource.Resource;
import org.licket.core.resource.ResourceStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author activey
 */
public class SpringResourceStorage implements ResourceStorage {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringResourceStorage.class);

  @Autowired
  private ServletContext servletContext;

  @Autowired
  private Collection<Resource> allResources;

  @Autowired
  private HeadParticipatingResource[] headParticipatingResources;

  @Autowired
  private FootParticipatingResource[] footParticipatingResources;

  private List<Resource> dynamicResources = newArrayList();

  private Predicate<Resource> byName(String name) {
    return resource -> resource.getName().equals(name);
  }

  private Predicate<Resource> byMimetype(String mimetype) {
    return resource -> resource.getMimeType().equals(mimetype);
  }

  @Override
  public boolean hasResource(String name, String mimetype) {
    return concat(allResources.stream(), dynamicResources.stream())
        .anyMatch(byName(name).and(byMimetype(mimetype)));
  }

  @Override
  public void putResource(Resource resource) {
    if (hasResource(resource.getName(), resource.getMimeType())) {
      LOGGER.debug("Resource with name = {} and mimetype = {} is already there, skipping...",
          resource.getName(), resource.getMimeType());
      return;
    }
    dynamicResources.add(resource);
  }

  @Override
  public void replaceResourceContent(Resource resource, byte[] newResourceContent) {
    if (!hasResource(resource.getName(), resource.getMimeType())) {
      LOGGER.debug(
          "Unable to find existing resource with name = {} and mimetype = {}, skipping content replacement ...",
          resource.getName(), resource.getMimeType());
        return;
    }
  }

  @Override
  public Optional<Resource> getResource(String name) {
    return concat(allResources.stream(), dynamicResources.stream()).filter(byName(name))
        .findFirst();
  }

  @Override
  public Stream<HeadParticipatingResource> getHeadJavascriptResources() {
    return stream(headParticipatingResources).filter(byMimetype(JAVASCRIPT_MIMETYPE));
  }

  @Override
  public Stream<FootParticipatingResource> getFootJavascriptResources() {
    return stream(footParticipatingResources).filter(byMimetype(JAVASCRIPT_MIMETYPE));
  }

  @Override
  public Stream<HeadParticipatingResource> getStylesheetResources() {
    return stream(headParticipatingResources).filter(byMimetype(CSS_MIMETYPE));
  }

  @Override
  public String getResourceUrl(Resource resource) {
    return format("%s%s/%s", servletContext.getContextPath(), CONTEXT_RESOURCES,
        resource.getName());
  }
}
