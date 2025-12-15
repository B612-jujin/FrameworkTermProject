package kr.ac.kopo.cjj.myapp.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackView {
    private String category;
    private Integer rating;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
    private String relativeTime;
}
