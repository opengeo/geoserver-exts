package org.opengeo.data.importer.format;

import java.io.IOException;
import java.io.InputStream;

import org.geotools.data.FeatureReader;
import org.opengeo.data.importer.transform.KMLPlacemarkTransform;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class KMLTransformingFeatureReader implements
        FeatureReader<SimpleFeatureType, SimpleFeature> {

    private final SimpleFeatureType featureType;

    private final FeatureReader<SimpleFeatureType, SimpleFeature> reader;

    private static final KMLPlacemarkTransform placemarkTransformer = new KMLPlacemarkTransform();

    public KMLTransformingFeatureReader(SimpleFeatureType featureType, InputStream inputStream) {
        this(featureType, new KMLRawFeatureReader(inputStream, featureType));
    }

    public KMLTransformingFeatureReader(SimpleFeatureType featureType,
            FeatureReader<SimpleFeatureType, SimpleFeature> reader) {
        this.featureType = featureType;
        this.reader = reader;
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public boolean hasNext() {
        try {
            return reader.hasNext();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SimpleFeature next() {
        SimpleFeature feature;
        try {
            feature = (SimpleFeature) reader.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SimpleFeature transformedFeature = placemarkTransformer
                .convertFeature(feature, featureType);
        return transformedFeature;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
