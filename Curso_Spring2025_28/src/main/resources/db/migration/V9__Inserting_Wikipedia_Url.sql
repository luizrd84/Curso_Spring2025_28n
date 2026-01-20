UPDATE person
SET wikipedia_profile_url = CASE 
    WHEN id = 1 THEN 'https://en.wikipedia.org/wiki/Ayrton_Senna'
    ELSE wikipedia_profile_url
END
WHERE id <= 18;