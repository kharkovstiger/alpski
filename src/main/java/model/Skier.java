package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import model.utils.Sex;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document
@Data
public class Skier implements Serializable{
    @Id
    private String Id;
    private String firstName;
    private String lastName;
    @JsonManagedReference
    @DBRef
    private Team team;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime birthday;//or in terms of game season and day
    private Sex sex;
}
