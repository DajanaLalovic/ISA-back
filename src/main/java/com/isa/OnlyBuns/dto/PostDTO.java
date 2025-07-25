package com.isa.OnlyBuns.dto;
import com.isa.OnlyBuns.model.Post;
import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {


    private Integer id;
    private String description;
    private String imagePath;
    private LocationDTO location;
    private LocalDateTime createdAt;
    private long userId;
    private List<CommentDTO> comments;
    private List<Integer> likes;
    private String imageBase64;
    private boolean isRemoved;

    public PostDTO() {
    }

    public PostDTO(Post post) {
        this.id = post.getId();
        this.description = post.getDescription();
        this.imagePath = post.getImagePath();
        if (post.getLocation() != null) {
            this.location = new LocationDTO(post.getLocation().getLatitude(), post.getLocation().getLongitude());
        }
        this.createdAt = post.getCreatedAt();
        this.userId = post.getUserId();
        this.isRemoved = post.getIsRemoved();

        // Mapirajte komentare u CommentDTO
        this.comments = post.getComments()
                .stream()
                .map(CommentDTO::new)
                .toList(); // Pretvori komentare u DTO, bez rekurzije


        // Mapirajte lajkove
        this.likes = post.getLikes();
    }


    public PostDTO(Integer id, String description,String imagePath, LocationDTO location, LocalDateTime createdAt, long userId,boolean isRemoved,List<CommentDTO> comments,List<Integer> likes) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.imagePath = imagePath;

        this.createdAt = createdAt;
        this.userId = userId;
        this.isRemoved = isRemoved;
        this.comments = comments;
        this.likes = likes;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }


    public long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean getIsRemoved() {
        return isRemoved;
    }
    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<Integer> getLikes() {
        return likes;
    }

    public void setLikes(List<Integer> likes) {
        this.likes = likes;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", location=" + location +
                ", createdAt=" + createdAt +
                ", userId=" + userId +
                ", comments=" + comments +
                ", likes=" + likes +
                ", imageBase64='" + imageBase64 + '\'' +
                ", isRemoved=" + isRemoved +
                '}';
    }
}

