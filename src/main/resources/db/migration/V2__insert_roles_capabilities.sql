-- Insert roles
INSERT INTO roles (name)
VALUES
    ('ADMIN'),
    ('CITIZEN');

-- Insert capabilities
INSERT INTO capabilities (name, description)
VALUES
    ('VIEW_CITIZEN',       'View Citizen and details'),
    ('VIEW_CITIZENS',      'View Citizen list and details'),
    ('DEACTIVATE_CITIZEN', 'De-activate Citizen account'),
    ('ACTIVATE_CITIZEN',   'Activate Citizen account'),
    ('VIEW_ONLY_CITIZEN',  'View only own Citizen details'),
    ('DEACTIVATE_ONLY_CITIZEN','De-activate only own Citizen account');

-- Apply capabilities to ADMIN
INSERT INTO roles_capabilities (role_id, capability_id)
SELECT r.id, c.id
FROM roles r, capabilities c
WHERE r.name = 'ADMIN'
  AND c.name IN ('VIEW_CITIZEN', 'VIEW_CITIZENS', 'DEACTIVATE_CITIZEN', 'ACTIVATE_CITIZEN');

-- Apply capabilities to CITIZEN
INSERT INTO roles_capabilities (role_id, capability_id)
SELECT r.id, c.id
FROM roles r, capabilities c
WHERE r.name = 'CITIZEN'
  AND c.name IN ('VIEW_ONLY_CITIZEN', 'DEACTIVATE_ONLY_CITIZEN');