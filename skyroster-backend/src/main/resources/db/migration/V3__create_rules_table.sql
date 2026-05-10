CREATE TABLE IF NOT EXISTS rules (
  id UUID PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT,
  rule_type TEXT,
  max_monthly_hours INT,
  min_rest_minutes INT,
  min_flights INT,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE
);



CREATE INDEX IF NOT EXISTS idx_rules_rule_type ON rules(rule_type);
CREATE INDEX IF NOT EXISTS idx_rules_active ON rules(active);
