## Table of Contents
[1. Cookbook Recipes](#cookbook-recipes)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Getting the Result Size of a SearchRequest](#getting-the-result-size-of-a-searchrequest)<br>
# Cookbook Recipes
## Getting the Result Size of a SearchRequest

Use `SearchDAO.search` to execute any `SearchRequest`. Use the returning `SearchResponse` to get the hits count. Use `SearchResponseMapper` to map the `SearchResponse` to your model.

