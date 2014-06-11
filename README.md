# Bloomf

Bloom filters in Clojure.

## Usage

### Install

![](https://clojars.org/bloomf/latest-version.svg)

### Bring you own hash functions
Bloomf does not provide hash functions. Bloomf is tested using `clojure.core/hash`

## Reference

### bloomf.core/make
	(make size hashers)

Creates a Bloom filter of the given size using the given hash functions. `hashers` must be a collection of unary functions returning integers.

### bloomf.core/add
	(add bloom x)
	(add bloom x & xs)

Adds x to the Bloom filter.

### bloomf.core/contains?
	(contains? bloom x)

Returns false if x is not in the Bloom filter.

### bloomf.core/intersection
	(intersection f1 f2)
	(intersection f1 f2 & fs)

Returns a Bloom filter that is the intersection of the inputs. The intersection might cause an increase of error rate.

### bloomf.core/union

	(union f1 f2)
	(union f1 f2 & fs)


Returns a Bloom filter that is the union of the inputs. The union operation does not affect the accuracy of the Bloom filter.

### bloomf.core/shrink
	(shrink bloom)

Returns a Bloom filter that is half to size of the input.

## License

Copyright © 2014

Distributed under the Eclipse Public License version 1.0.
