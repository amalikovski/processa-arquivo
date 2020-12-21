package br.com.processaarquivo.repositories;


import br.com.processaarquivo.model.InputData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputDataRepository extends JpaRepository<InputData, Long> {

}
