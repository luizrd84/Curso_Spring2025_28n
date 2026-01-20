-- Atualizar os campos de photo_url para as 12 primeiras pessoas famosas
UPDATE person
SET photo_url = CASE 
    WHEN id = 1 THEN 'https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/01_senna.jpg'
    ELSE photo_url
END
WHERE id <= 18;
