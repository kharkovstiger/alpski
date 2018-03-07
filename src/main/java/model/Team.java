package model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Document
public class Team implements Serializable{
    @Id
    private String Id;
    private String teamName;
    @JsonBackReference
    @DBRef
    private List<Skier> skiers;
    @JsonManagedReference
    @DBRef
    private User owner;
}
