<?php
/**
 * Copyright 2010-2014 baas-platform.com, Pty Ltd. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

namespace Apache\Usergrid\Tests\Api;


use Apache\Usergrid\Api\Exception\UsergridException;
use PHPUnit_Framework_TestCase;

/**
 * Class UsergridTest
 *
 * @package    Apache/Usergrid
 * @version    1.0.0
 * @author     Jason Kristian <jasonkristian@gmail.com>
 * @license    Apache License, Version  2.0
 * @copyright  (c) 2008-2014, Baas Platform Pty. Ltd
 * @link       http://baas-platform.com
 */
class ApplicationTest extends PHPUnit_Framework_TestCase
{

    /**
     * Usergrid client
     *
     * @var Usergrid $usergrid
     */
    protected $usergrid;

    protected $config;

    /**
     *
     */
    public function setUp()
    {
        $this->usergrid = $GLOBALS['usergrid'];
    }

    /**
     * @test
     * @group internet
     */
    public function it_can_get_entity()
    {

        $error = null;

        try {
            $this->usergrid->application()->EntityGet(['collection' => 'users']);
        } catch (UsergridException  $e) {
            $error = $e;
        }

        $this->assertNull($error, 'Exception should be null');
    }


}
