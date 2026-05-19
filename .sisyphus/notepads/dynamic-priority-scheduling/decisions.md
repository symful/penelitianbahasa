# Decisions: Dynamic Priority Scheduling

## Made

| Decision | Value | Source |
|-----------|-------|--------|
| CSV Format | `id,name,studentId,lksCount,difficulty,deadline,estimatedMinutes` | User confirmed Format 1 |
| Weights | Equal [1.0, 1.0, 1.0, 1.0] | User said "Idk much" → sensible default |
| Accuracy Definition | Heap ordering correctness (pairwise ≥95%) | Default assumption |
| Java Version | 17 | Modern JMH requirement |
| Build Tool | Maven | JMH ecosystem best support |

## Pending

| Decision | Options | Status |
|----------|---------|--------|
| None - all resolved | - | - |