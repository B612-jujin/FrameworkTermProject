package kr.ac.kopo.cjj.myapp.service;

import java.util.List;
import java.util.Random;
import kr.ac.kopo.cjj.myapp.domain.Portfolio;
import kr.ac.kopo.cjj.myapp.domain.UserAccount;
import kr.ac.kopo.cjj.myapp.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final TechStackService techStackService;
    private final Random random = new Random();

    public PortfolioService(PortfolioRepository portfolioRepository, TechStackService techStackService) {
        this.portfolioRepository = portfolioRepository;
        this.techStackService = techStackService;
    }

    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public List<Portfolio> getVisiblePortfolios() {
        return portfolioRepository.findByVisibleTrue();
    }

    public List<Portfolio> filterPortfolios(boolean isAdmin, List<String> techSelections, String keyword) {
        List<Portfolio> base = isAdmin ? portfolioRepository.findAll() : portfolioRepository.findByVisibleTrue();
        return base.stream()
                .filter(p -> matchTech(p, techSelections))
                .filter(p -> matchKeyword(p, keyword))
                .toList();
    }

    public List<Portfolio> getPortfoliosByUser(String username) {
        return portfolioRepository.findByOwnerUsername(username);
    }

    public Portfolio getById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다: " + id));
    }

    @Transactional
    public Portfolio createPortfolio(Portfolio portfolio, UserAccount owner) {
        if (owner != null) {
            portfolio.setOwner(owner);
        }
        if (portfolio.getVisible() == null) {
            portfolio.setVisible(true);
        }
        if (portfolio.getRandomColor() == null || portfolio.getRandomColor().isBlank()) {
            portfolio.setRandomColor(generateRandomColor());
        }
        applyThemeFallbacks(portfolio);
        return portfolioRepository.save(portfolio);
    }

    @Transactional
    public Portfolio updatePortfolio(Long id, Portfolio updated) {
        Portfolio existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setImageUrl(updated.getImageUrl());
        existing.setProjectLink(updated.getProjectLink());
        existing.setExtraUrl(updated.getExtraUrl());
        existing.setTechStack(updated.getTechStack());
        existing.setRandomColor(updated.getRandomColor());
        existing.setVisible(updated.getVisible() != null ? updated.getVisible() : existing.getVisible());
        applyThemeFallbacks(existing);
        return portfolioRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!portfolioRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 포트폴리오가 없습니다: " + id);
        }
        portfolioRepository.deleteById(id);
    }

    @Transactional
    public void toggleVisibility(Long id) {
        Portfolio p = getById(id);
        boolean current = Boolean.TRUE.equals(p.getVisible());
        p.setVisible(!current);
        portfolioRepository.save(p);
    }

    private void applyThemeFallbacks(Portfolio portfolio) {
        boolean hasImage = portfolio.getImageUrl() != null && !portfolio.getImageUrl().isBlank();
        boolean hasColor = portfolio.getRandomColor() != null && !portfolio.getRandomColor().isBlank();
        if (!hasImage && !hasColor) {
            portfolio.setRandomColor(generateRandomColor());
        }
    }

    private String generateRandomColor() {
        return String.format("#%06x", random.nextInt(0x1000000));
    }

    private boolean matchTech(Portfolio p, List<String> techSelections) {
        if (techSelections == null || techSelections.isEmpty()) return true;
        if (p.getTechStack() == null) return false;
        String ts = p.getTechStack().toLowerCase();
        return techSelections.stream()
                .filter(t -> t != null && !t.isBlank())
                .allMatch(t -> ts.contains(t.toLowerCase()));
    }

    private boolean matchKeyword(Portfolio p, String keyword) {
        if (keyword == null || keyword.isBlank()) return true;
        String k = keyword.toLowerCase();
        return (p.getTitle() != null && p.getTitle().toLowerCase().contains(k))
                || (p.getDescription() != null && p.getDescription().toLowerCase().contains(k))
                || (p.getTechStack() != null && p.getTechStack().toLowerCase().contains(k));
    }
}
