ALTER TABLE SB_TB_APPOINTMENT ADD (
    animal_name VARCHAR2(255),
    guardian_name VARCHAR2(255)
);

ALTER TABLE SB_TB_CHECK_UP ADD (
    animal_name VARCHAR2(255),
    guardian_name VARCHAR2(255)
);

UPDATE SB_TB_APPOINTMENT ap SET
    animal_name = (SELECT a.name FROM SB_TB_ANIMAL a WHERE a.id = ap.animal_id),
    guardian_name = (SELECT g.name FROM SB_TB_ANIMAL a
                     JOIN SB_TB_GUARDIAN g ON g.id = a.guardian_id
                     WHERE a.id = ap.animal_id);

UPDATE SB_TB_CHECK_UP cu SET
    animal_name = (SELECT a.name FROM SB_TB_ANIMAL a WHERE a.id = cu.animal_id),
    guardian_name = (SELECT g.name FROM SB_TB_ANIMAL a
                     JOIN SB_TB_GUARDIAN g ON g.id = a.guardian_id
                     WHERE a.id = cu.animal_id);

ALTER TABLE SB_TB_APPOINTMENT MODIFY (animal_id NULL);
ALTER TABLE SB_TB_APPOINTMENT DROP CONSTRAINT fk_appointment_animal;
ALTER TABLE SB_TB_APPOINTMENT ADD CONSTRAINT fk_appointment_animal
    FOREIGN KEY (animal_id) REFERENCES SB_TB_ANIMAL(id) ON DELETE SET NULL;

ALTER TABLE SB_TB_CHECK_UP MODIFY (animal_id NULL);
ALTER TABLE SB_TB_CHECK_UP DROP CONSTRAINT fk_check_up_animal;
ALTER TABLE SB_TB_CHECK_UP ADD CONSTRAINT fk_check_up_animal
    FOREIGN KEY (animal_id) REFERENCES SB_TB_ANIMAL(id) ON DELETE SET NULL;

ALTER TABLE SB_TB_HISTORY DROP CONSTRAINT fk_history_animal;
ALTER TABLE SB_TB_HISTORY ADD CONSTRAINT fk_history_animal
    FOREIGN KEY (animal_id) REFERENCES SB_TB_ANIMAL(id) ON DELETE CASCADE;

ALTER TABLE SB_TB_ANIMAL_VACCINATION DROP CONSTRAINT fk_animal_vaccination_animal;
ALTER TABLE SB_TB_ANIMAL_VACCINATION ADD CONSTRAINT fk_animal_vaccination_animal
    FOREIGN KEY (animal_id) REFERENCES SB_TB_ANIMAL(id) ON DELETE CASCADE;
