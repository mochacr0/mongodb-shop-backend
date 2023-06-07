package com.example.springbootmongodb.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FailedLoginAttempt{
    private int count;
    private long firstAttemptMillis;
    private boolean isEnabled;
    private long lockExpirationMillis;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FailedLoginAttemptEntity [count=");
        builder.append(this.getCount());
        builder.append(", firstFailedAttemptMillis=");
        builder.append(this.getFirstAttemptMillis());
        builder.append(", isEnabled=");
        builder.append(this.isEnabled());
        builder.append(", lockExpirationMillis=");
        builder.append(this.getLockExpirationMillis());
        return builder.toString();
    }

}
