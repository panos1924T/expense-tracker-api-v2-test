INSERT INTO capabilities (name, description)
VALUES
    ('EDIT_ONLY_CITIZEN', 'Edit only own Citizen details');

INSERT INTO roles_capabilities (role_id, capability_id)
SELECT r.id, c.id FROM roles r, capabilities c
WHERE r.name = 'CITIZEN' AND c.name = 'EDIT_ONLY_CITIZEN';