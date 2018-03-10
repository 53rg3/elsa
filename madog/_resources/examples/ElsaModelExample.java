package assets;

import model.ElsaIndexData;
import model.ElsaModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Video implements ElsaModel {

    private static final ElsaIndexData indexData = new ElsaIndexData.Builder()
            .setIndexName("youtube")
            .setType("video")
            .setShards(1)
            .setReplicas(1)
            .build();

    @Id // ID field will be set by elastic automatically
    private String id;

    @Field(type = FieldType.text)
    private String title;

    @Field(type = FieldType.text)
    private String url;

    @Field(type = FieldType.text)
    private String tags;

    @Override
    public ElsaIndexData getIndexData() {
        return indexData;
    }
}
