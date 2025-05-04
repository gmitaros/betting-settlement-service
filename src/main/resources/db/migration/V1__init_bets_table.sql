-- Create bets table
CREATE TABLE bets
(
    bet_id          UUID PRIMARY KEY,
    user_id         UUID           NOT NULL,
    event_id        UUID           NOT NULL,
    event_market_id VARCHAR(100)   NOT NULL,
    event_winner_id VARCHAR(100),
    bet_amount      NUMERIC(12, 2) NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_bets_event_id ON bets (event_id);
CREATE INDEX idx_bets_user_id ON bets (user_id);
CREATE INDEX idx_bets_event_market ON bets (event_id, event_market_id);
