package kr.ac.kopo.cjj.myapp.service;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kopo.cjj.myapp.domain.Feedback;
import kr.ac.kopo.cjj.myapp.domain.Portfolio;
import kr.ac.kopo.cjj.myapp.domain.UserAccount;
import kr.ac.kopo.cjj.myapp.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public List<Feedback> getByPortfolio(Long portfolioId) {
        return feedbackRepository.findByPortfolioIdOrderByCreatedAtDesc(portfolioId);
    }

    @Transactional
    public Feedback addFeedback(Portfolio portfolio, UserAccount author, String content, String category, Integer rating) {
        Feedback feedback = Feedback.builder()
                .portfolio(portfolio)
                .author(author)
                .content(content)
                .category(category)
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .build();
        return feedbackRepository.save(feedback);
    }
}
