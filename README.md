# In-memory data structure that acts as a cache.
- The cache bounded in size to a maximum value
- Keys and values are generic types
- Entries in the cache are invalidated after a given amount of time
- Entries in the cache are evicted by least frequency usage - LFU
- Test-cases for the cache are defined and called in the main() for demo purposes

Implemented the freqHM cacheLHM version because of the trade-offs listed below.

## Options:

### freqHM cacheLHM - LRU operation worse case
- GET, rm & add key to new freq leve in freqHM O(1)
- PUT existing entry, update entry in HM O(1)
- PUT new entry, add tp LHM & update freqHM O(1)
- PUT new entry and rm LFU, rm in HM & LFM O(1)
- Remove LRU O(n) if it removed a min freq entry and finds new minf,
	else O(1) + iterating items in LHM LRU

### freqPQ cacheHM - LRU operation worse case
- GET has to find and update(rm,add freq), offer element to ensure count and consistency O(n)
- PUT existing entry, has to to find and update, offer element to ensure count and consistency O(n)
- PUT new entry, add item, offer O(log n) to ensure count and consistency
- PUT new entry and pop LFU, O(1) to ensure count and consistency
- Remove LRU has to find and rm element O(n)


### Developer: Leon Li
