-- Table: public.downtime_code

-- DROP TABLE public.downtime_code;

CREATE TABLE IF NOT EXISTS public.downtime_code
(
    id uuid NOT NULL,
    search_text character varying(255) COLLATE pg_catalog."default" NOT NULL,
    customer_id uuid,
    tenant_id uuid NOT NULL,
    created_time bigint NOT NULL,
    level1 character varying(255) COLLATE pg_catalog."default" NOT NULL,
    level2 character varying(255) COLLATE pg_catalog."default",
    level3 character varying(255) COLLATE pg_catalog."default",
    code numeric NOT NULL,
    CONSTRAINT downtime_code_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE public.downtime_code
    OWNER to postgres;


-- Table: public.shift

-- DROP TABLE public.shift;

CREATE TABLE IF NOT EXISTS public.shift
(
    id uuid NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    area_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    search_text character varying(255) COLLATE pg_catalog."default",
    customer_id uuid,
    tenant_id uuid,
    created_time bigint NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint NOT NULL,
    CONSTRAINT shift_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE public.shift
    OWNER to postgres;