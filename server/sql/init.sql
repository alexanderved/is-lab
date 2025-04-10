CREATE DOMAIN integer_nonnull AS integer CHECK(VALUE IS NOT NULL);
CREATE DOMAIN bigint_nonnull AS bigint CHECK(VALUE IS NOT NULL);
CREATE DOMAIN real_nonnull AS real CHECK(VALUE IS NOT NULL);

CREATE TYPE coordinates AS (
    x real_nonnull,
    y integer_nonnull
);

CREATE TYPE location AS (
    x bigint_nonnull,
    y bigint_nonnull,
    z integer_nonnull
);

CREATE TYPE organization_type AS ENUM (
    'COMMERCIAL',
    'PUBLIC',
    'TRUST',
    'PRIVATE_LIMITED_COMPANY',
    'OPEN_JOINT_STOCK_COMPANY'
);

CREATE TABLE IF NOT EXISTS organizations(
    id integer PRIMARY KEY CHECK(id > 0),
    name text NOT NULL CHECK(LENGTH(name) > 0),
    coordinates coordinates NOT NULL,
    creation_date timestamp NOT NULL,
    creation_date_zone text NOT NULL,
    annual_turnover real CHECK(annual_turnover > 0),
    full_name text,
    employees_count integer NOT NULL CHECK(employees_count > 0),
    type organization_type NOT NULL,
    street text NOT NULL CHECK(LENGTH(street) > 0),
    town location NOT NULL
);