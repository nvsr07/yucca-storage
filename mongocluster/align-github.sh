#!/bin/bash
cp --parents -r `hg locate | xargs` $1
