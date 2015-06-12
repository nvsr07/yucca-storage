from setuptools import setup, find_packages
import os

here = os.path.abspath(os.path.dirname(__file__))
try:
    README = open(os.path.join(here, 'README.rst')).read()
except IOError:
    README = ''

VERSION = "0.0.1"

INSTALL_REQUIREMENTS = [
    'pymongo==3.0.2',
]

TEST_REQUIREMENTS = [
    'nose',
    'coverage'
]


setup(
    name='yuccausage',
    version=VERSION,
    description="Gather cross tenant usage statistics for YUCCA platform",
    long_description=README,
    classifiers=[],
    keywords='',
    author='AXANT',
    author_email='info@axant.it',
    url='',
    license='',
    packages=find_packages(exclude=['ez_setup', 'examples', 'tests']),
    include_package_data=True,
    zip_safe=False,
    install_requires=INSTALL_REQUIREMENTS,
    extras_require={
        'testing': TEST_REQUIREMENTS,
    },
    test_suite='nose.collector',
    tests_require=TEST_REQUIREMENTS,
    entry_points={
        'console_scripts': [
            'yuccausage = yuccausage.__main__:main',
        ]
    }
)
