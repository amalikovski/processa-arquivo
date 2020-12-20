package br.com.processaarquivo.vo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputDataRepository extends JpaRepository<InputData, Long> {

}
