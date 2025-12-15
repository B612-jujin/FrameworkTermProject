package kr.ac.kopo.cjj.myapp.service;

import java.util.List;
import kr.ac.kopo.cjj.myapp.domain.FeedbackTheme;
import kr.ac.kopo.cjj.myapp.repository.FeedbackThemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackThemeService {

    private final FeedbackThemeRepository repository;

    public FeedbackThemeService(FeedbackThemeRepository repository) {
        this.repository = repository;
    }

    public List<FeedbackTheme> findAll() {
        return repository.findAll();
    }

    @Transactional
    public FeedbackTheme add(String name) {
        if (repository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("이미 존재하는 테마입니다.");
        }
        return repository.save(FeedbackTheme.builder().name(name).build());
    }

    @Transactional
    public FeedbackTheme update(Long id, String name) {
        FeedbackTheme t = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("테마를 찾을 수 없습니다."));
        t.setName(name);
        return repository.save(t);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
