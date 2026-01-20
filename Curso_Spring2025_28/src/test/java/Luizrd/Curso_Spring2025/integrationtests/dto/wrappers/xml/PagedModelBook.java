package Luizrd.Curso_Spring2025.integrationtests.dto.wrappers.xml;


import Luizrd.Curso_Spring2025.integrationtests.dto.BookDTO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
public class PagedModelBook { 
	
	@XmlElement(name = "content") 
	private List<BookDTO> content;

	public PagedModelBook() {}

	public List<BookDTO> getContent() {
		return content;
	}

	public void setContent(List<BookDTO> content) {
		this.content = content;
	}
}
