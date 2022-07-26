import coloredlogs, logging
logger = logging.getLogger("logger")
coloredlogs.install(level='DEBUG', logger=logger)


def logException(e):
    logger.critical("Exception: " + str(e))
