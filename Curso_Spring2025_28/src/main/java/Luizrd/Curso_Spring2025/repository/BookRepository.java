package Luizrd.Curso_Spring2025.repository;

import Luizrd.Curso_Spring2025.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {


}
