
def _run_pipeline(steps, origin, **kwargs):
    """Given a list of functions runs them in a pipeline.

    Each function receives the output of the previous function as
    its input and all the keyword arguments provided to the
    pipeline. Output **must be a dictionary**.

    """
    output = origin
    for step in steps:
        output, kwargs = step(output, **kwargs)
    return output
