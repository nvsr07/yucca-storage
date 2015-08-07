#!/bin/bash
rsync -R `hg locate | xargs` $1
