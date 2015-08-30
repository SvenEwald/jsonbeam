# JsonBeam

Java data projection for JSON

Idea

 - Provide access to JSON data via JSONPath like queries (like XMLBeam does)
 - Speed up JSONPath evaluation by evaluating all queries at once during document parsing
 - Avoid creation of DOM like structures, use indexes instead
 
Current state: version 0.0.2

 - Still experimental, API still may be changed 
 - There are a lot of not yet implemented corner cases
 - Type conversion is not fully implemented yet (missing: PrimitiveStreams, custom value objects, maps)
 - JSONPath implementation is not complete yet (predicates)
 - Performance measurements are very promising
 - Works well with small (<1Kb) and very huge (>200Mb) documents

Design goals:

JSON
 - support exotic deep data structures by using a nonrecursive parser
 - small memory foodprint for large files by creating a compressed index instead of an object graph
 - high performance by avoiding object allocation and buffer copies when possible
 - support for compact json variants (nonquoted keys & values)

Data Projection
 - read support by using a variant of JSONPath
 - write support for deletes & updates 
 - write support for JSON structures

Read & write operations
 - try to minimise output changes to get a clean diff

Java
 - Java 8 and higher are supported
 - Rich use of Java 8 features internally and in the API
 - Fully statically typed API
 
Dual License
 - GPL V3 for open source projects
 - Comercial license available later (version 1.0.0 and higher)
 - Backports for pre Java 8 available under comercial licence later (version 1.0.0 and higher)
