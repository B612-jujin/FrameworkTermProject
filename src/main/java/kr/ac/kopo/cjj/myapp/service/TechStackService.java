package kr.ac.kopo.cjj.myapp.service;

import java.util.List;
import kr.ac.kopo.cjj.myapp.domain.TechStack;
import kr.ac.kopo.cjj.myapp.repository.TechStackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TechStackService {

    private final TechStackRepository techStackRepository;

    public TechStackService(TechStackRepository techStackRepository) {
        this.techStackRepository = techStackRepository;
    }

    public List<TechStack> findAll() {
        return techStackRepository.findAll();
    }

    @Transactional
    public TechStack add(String name) {
        if (techStackRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("이미 존재하는 기술입니다.");
        }
        return techStackRepository.save(TechStack.builder().name(name).build());
    }

    @Transactional
    public TechStack update(Long id, String name) {
        TechStack t = techStackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기술을 찾을 수 없습니다."));
        t.setName(name);
        return techStackRepository.save(t);
    }

    @Transactional
    public void delete(Long id) {
        techStackRepository.deleteById(id);
    }
}
