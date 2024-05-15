#!/bin/bash
set -e

# Check if the data directory is empty
if [ -z "$(ls -A /var/lib/postgresql/data)" ]; then
    echo "Initializing new database..."
    # Ensure the PostgreSQL server is running before creating roles and replication slots
    pg_ctl -D "$PGDATA" -o "-c listen_addresses='*'" -w start

    # Run the psql command to create the replication user and replication slot
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
CREATE USER replication_user WITH REPLICATION ENCRYPTED PASSWORD 'replication_password';
SELECT pg_create_physical_replication_slot('replication_slot') WHERE NOT EXISTS (SELECT 1 FROM pg_replication_slots WHERE slot_name = 'replication_slot');
EOSQL

    # Stop the PostgreSQL server after initialization
    pg_ctl -D "$PGDATA" -m fast -w stop
else
    echo "Database directory is not empty, skipping initialization."
fi
